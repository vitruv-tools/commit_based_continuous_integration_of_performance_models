package cipm.consistency.util.build.server.updatesite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/administration")
public class AdministrationController {
    @Autowired
    private ConfigurableApplicationContext context;

    @GetMapping("/status")
    public Status status() {
        return new Status();
    }

    @GetMapping("/stop")
    @ResponseStatus(HttpStatus.OK)
    public void stop() {
        context.close();
    }
}
