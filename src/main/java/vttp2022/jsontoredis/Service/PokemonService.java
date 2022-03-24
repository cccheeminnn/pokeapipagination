package vttp2022.jsontoredis.Service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import vttp2022.jsontoredis.Model.Pokemon;
import vttp2022.jsontoredis.Repository.PokemonRepository;

@Service
public class PokemonService {
    
    @Autowired
    @Qualifier("games")
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PokemonRepository pkmRepo;

    public List<Pokemon> retrievePokemonList(String page) 
    {
        List<String> pkmListString = pkmRepo.retrievePageData(page);
        
        //instantiate a List<Pokemon> to parse in the 10 pokemon to return 
        List<Pokemon> pkmListObj = new ArrayList<>();
        pkmListString.stream().forEach(s -> {
            //the string was converted from a JsonObject.toString, 
            //to reverse this process we need to use new StringReader()
            JsonReader reader = Json.createReader(new StringReader(s));
            JsonObject obj = reader.readObject();
            
            //instantiate a new Pokemon obj and set the parameters
            Pokemon pkm = new Pokemon();
            pkm.setName(obj.getString("name"));
            pkm.setHeight(obj.getInt("height"));
            pkm.setImageLink(obj.getString("imageLink"));
            //add to a List<Pokemon>
            pkmListObj.add(pkm);
        });

        return pkmListObj;    
    }
}
