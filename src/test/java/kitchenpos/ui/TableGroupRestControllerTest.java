package kitchenpos.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import kitchenpos.application.TableGroupService;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TableGroupRestController.class)
class TableGroupRestControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TableGroupService tableGroupService;

    @Test
    void create() throws Exception {
        // given
        final TableGroup result = new TableGroup();
        result.setId(1L);
        given(tableGroupService.create(any())).willReturn(result);

        final TableGroup request = new TableGroup();
        request.setCreatedDate(LocalDateTime.now());

        // when
        mockMvc.perform(post("/api/table-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/api/table-groups/1"));
    }

    @Test
    void ungroup() throws Exception {
        // given
        willDoNothing().given(tableGroupService).ungroup(any());

        // when
        mockMvc.perform(delete("/api/table-groups/{tableGroupId}", 1L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
