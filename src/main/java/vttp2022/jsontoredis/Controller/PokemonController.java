package vttp2022.jsontoredis.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import vttp2022.jsontoredis.Model.Pokemon;
import vttp2022.jsontoredis.Service.PokemonService;

@Controller
@RequestMapping(path="")
public class PokemonController {
    
    @Autowired
    private PokemonService pkmSvc;

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
    @GetMapping(path="/save")
    public String getPokemonAndSave() 
    {
        //saveToRedis makes a api call to pokeapi
        pkmSvc.saveToRedis();
        return "HomePage";
    }

    //http://localhost:8080/pokemon/{page}, manually key in the page lol
    //probably can do some thymeleaf stuff but idk
    @GetMapping(path="/pokemon/{page}")
    public String getPokemonList(@PathVariable String page, Model m) 
    {
        List<Pokemon> pkmList = new ArrayList<>();
        //pagination works by parsing in the page to the method
        pkmList = pkmSvc.retrievePokemonList(page);

        m.addAttribute("page", page);
        m.addAttribute("pkmList", pkmList);
        return "page";
    }

}
