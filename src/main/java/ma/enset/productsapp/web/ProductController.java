package ma.enset.productsapp.web;

import lombok.Data;
import ma.enset.productsapp.repositories.ProductRepository;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProductController{


    @Autowired
    KeycloakRestTemplate keycloakRestTemplate;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/products")
    public String products(Model model){
        model.addAttribute("products",productRepository.findAll());
        return "products";
    }
    @GetMapping("/suppliers")
    public String suppliers(Model model){
        PagedModel<Supplier> suppliers=keycloakRestTemplate.getForObject("http://localhost:8083/suppliers",PagedModel.class);
        model.addAttribute("suppliers",suppliers);
        return "suppliers";
    }

    @GetMapping("/jwt")
    @ResponseBody
    public Map<String,String> map(HttpServletRequest request)
    {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal principal= (KeycloakPrincipal) token.getPrincipal();
        KeycloakSecurityContext keycloakSecurityContext=principal.getKeycloakSecurityContext();
        Map<String,String> map=new HashMap<>();
        map.put("access_token",keycloakSecurityContext.getTokenString());
        return map;
    }



    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e, Model model)
    {
        model.addAttribute("errorMessage","probleme d'authorisation");
        return "errors";
    }

}
@Data
class Supplier{
    private Long id;
    private String name;
    private String email;
}
