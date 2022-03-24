package vttp2022.jsontoredis.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vttp2022.jsontoredis.Model.Pokemon;
import vttp2022.jsontoredis.Repository.PokemonRepository;
import vttp2022.jsontoredis.Service.PokemonService;

@Controller
@RequestMapping(path="")
public class PokemonController {
    
    @Autowired
    private PokemonService pkmSvc;

    @Autowired
    private PokemonRepository pkmRepo;

    @Autowired
    @Qualifier("games")
    private RedisTemplate<String, String> redisTemplate;

    //enter url without any variable goes to homepage
    @GetMapping(path="")
    public String getHomePage() 
    {
        return "HomePage";
    }

    //homepage has a save button which goes back to homepage after pressing
    @GetMapping(path="/saved")
    public String getPokemonAndSave(Model m) 
    {
        //saveToRedis makes a api call to pokeapi
        pkmRepo.saveToRedis();

        List<Pokemon> pkmList = new ArrayList<>();
        //pagination works by parsing in the page to the method
        pkmList = pkmSvc.retrievePokemonList("1");

        Integer noOfPkm = pkmSvc.getPages();
        Integer noOfPages = noOfPkm/10;

        m.addAttribute("page", "1");
        m.addAttribute("pages", noOfPages);
        m.addAttribute("pkmList", pkmList);

        return "page";
    }

    //http://localhost:8080/pokemon/{page}, manually key in the page lol
    //probably can do some thymeleaf stuff but idk
    @GetMapping(path="/pokemon")
    public String getPokemonList(@RequestParam String pageNumber, Model m) 
    {
        if (pageNumber.equals("0")) {
            pageNumber = "1";
        }
        List<Pokemon> pkmList = new ArrayList<>();
        //pagination works by parsing in the page to the method
        pkmList = pkmSvc.retrievePokemonList(pageNumber);

        Integer noOfPkm = pkmSvc.getPages();
        Integer noOfPages = noOfPkm/10;

        m.addAttribute("page", pageNumber);
        m.addAttribute("pages", noOfPages);
        m.addAttribute("pkmList", pkmList);
        return "page";
    }

}
