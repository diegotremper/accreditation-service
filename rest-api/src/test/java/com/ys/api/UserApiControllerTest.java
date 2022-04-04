package com.ys.api;

import com.ys.accreditation.api.AccreditationServiceApplication;
import com.ys.accreditation.app.entity.UserId;
import com.ys.accreditation.app.service.accreditation.AccreditationProcessResult;
import com.ys.accreditation.app.service.accreditation.AccreditationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AccreditationServiceApplication.class})
@AutoConfigureMockMvc
class UserApiControllerTest {

  @MockBean AccreditationService accreditationService;
  @Autowired MockMvc mockMvc;

  @Test
  @DisplayName(
      "When the request content is not provided, should return a not acceptable status code")
  void testWithMissingBody() throws Exception {
    mockMvc
        .perform(post("/user/accreditation").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotAcceptable())
        .andExpect(jsonPath("$.errors", hasSize(1)))
        .andExpect(jsonPath("$.errors[0]", containsString("Required request body is missing")))
        .andExpect(jsonPath("$.code", is("BadRequest")));
  }

  @Test
  @DisplayName(
      "When a valid accreditation request is provided, should delegate the request to the application")
  void testWithExpectedBody() throws Exception {
    Mockito.when(accreditationService.validate(Mockito.any(), Mockito.any()))
        .thenReturn(AccreditationProcessResult.builder().accredited(true).success(true).build());

    mockMvc
        .perform(
            post("/user/accreditation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n"
                        + "    \"user_id\": \"g8NlYJnk7zK9BlB1J2Ebjs0AkhCTpE1V\",\n"
                        + "    \"payload\": {\n"
                        + "        \"accreditation_type\": \"BY_INCOME\",\n"
                        + "        \"documents\": [\n"
                        + "            {\n"
                        + "                \"name\": \"qweqwe.pdf\",\n"
                        + "                \"mime_type\": \"application/pdf\",\n"
                        + "                \"content\": \"ICAiQC8qIjogWyJzcmMvKiJdCiAgICB9CiAgfQp9Cg==\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(true)))
        .andExpect(jsonPath("$.accredited", is(true)));

    UserId expectedId = UserId.builder().id("g8NlYJnk7zK9BlB1J2Ebjs0AkhCTpE1V").build();
    Mockito.verify(accreditationService, Mockito.times(1))
        .validate(Mockito.eq(expectedId), Mockito.anyList());
  }

  @Test
  @DisplayName(
      "When the userId and payload are not provided, should return a bad request error by indicating the invalid parameters")
  void testEmptyUserIdAndPayload() throws Exception {
    mockMvc
        .perform(post("/user/accreditation").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code", is("BadRequest")))
        .andExpect(jsonPath("$.errors", hasSize(2)))
        .andExpect(
            jsonPath(
                "$.errors[*]",
                containsInAnyOrder(
                    "Invalid field 'payload': must not be null",
                    "Invalid field 'userId': must not be null")));
  }

  @Test
  @DisplayName(
      "When a invalid accreditation type is provided, should return a bad request response")
  void testWithInvalidAccreditationType() throws Exception {
    mockMvc
        .perform(
            post("/user/accreditation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n"
                        + "    \"user_id\": \"g8NlYJnk7zK9BlB1J2Ebjs0AkhCTpE1V\",\n"
                        + "    \"payload\": {\n"
                        + "        \"accreditation_type\": \"INVALID\",\n"
                        + "        \"documents\": [\n"
                        + "            {\n"
                        + "                \"name\": \"qweqwe.pdf\",\n"
                        + "                \"mime_type\": \"application/pdf\",\n"
                        + "                \"content\": \"ICAiQC8qIjogWyJzcmMvKiJdCiAgICB9CiAgfQp9Cg==\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code", is("BadRequest")))
        .andExpect(jsonPath("$.errors", hasSize(1)))
        .andExpect(jsonPath("$.errors[0]", containsString("Invalid field 'payload.accreditationType': must not be null")));
  }

  @Test
  @DisplayName(
      "When a invalid base64 content is provided in the documents section, should return a not acceptable response")
  void testWithInvalidFileContent() throws Exception {
    mockMvc
        .perform(
            post("/user/accreditation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n"
                        + "    \"user_id\": \"g8NlYJnk7zK9BlB1J2Ebjs0AkhCTpE1V\",\n"
                        + "    \"payload\": {\n"
                        + "        \"accreditation_type\": \"BY_INCOME\",\n"
                        + "        \"documents\": [\n"
                        + "            {\n"
                        + "                \"name\": \"qweqwe.pdf\",\n"
                        + "                \"mime_type\": \"application/pdf\",\n"
                        + "                \"content\": \"==\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}"))
        .andDo(print())
        .andExpect(status().isNotAcceptable())
        .andExpect(jsonPath("$.code", is("BadRequest")))
        .andExpect(
            jsonPath(
                "$.errors[0]",
                containsString("Failed to decode VALUE_STRING as base64 (MIME-NO-LINEFEEDS)")));
  }

  @Test
  @DisplayName(
      "When the application throws a unexpected errors, should return an internal server error")
  void testUnhandledError() throws Exception {
    Mockito.when(accreditationService.validate(Mockito.any(), Mockito.any()))
        .thenThrow(new RuntimeException("Unhandled exception"));

    mockMvc
        .perform(
            post("/user/accreditation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n"
                        + "    \"user_id\": \"g8NlYJnk7zK9BlB1J2Ebjs0AkhCTpE1V\",\n"
                        + "    \"payload\": {\n"
                        + "        \"accreditation_type\": \"BY_INCOME\",\n"
                        + "        \"documents\": [\n"
                        + "            {\n"
                        + "                \"name\": \"qweqwe.pdf\",\n"
                        + "                \"mime_type\": \"application/pdf\",\n"
                        + "                \"content\": \"ICAiQC8qIjogWyJzcmMvKiJdCiAgICB9CiAgfQp9Cg==\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}"))
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is("InternalServerError")))
        .andExpect(jsonPath("$.error", containsString("Unhandled exception")));
  }
}
