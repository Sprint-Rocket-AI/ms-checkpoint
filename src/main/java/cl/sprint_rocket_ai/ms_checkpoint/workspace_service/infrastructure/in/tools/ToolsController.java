package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.tools.CrearListaFormateadaParaBD;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tools")
public final class ToolsController implements ToolsRest {

    @Override
    @PostMapping("/format-in")
    public ResponseEntity<FormatToListResponse> formatearIn(@RequestBody FormatToListRequest request) {
        FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);
        return ResponseEntity.ok(response);
    }
}

