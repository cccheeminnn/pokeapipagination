package vttp2022.jsontoredis.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import vttp2022.jsontoredis.Model.Pokemon;

@Service
public class PokemonService {
    
    @Autowired
    @Qualifier("games")
    private RedisTemplate<String, String> redisTemplate;

    public void saveToRedis() {

        //flushall redis db
        redisTemplate.delete("pokemons");

        //apiurl - changing limit changes the number of pokemon to save
        //offset is like start saving from which index
        String apiurl = "https://pokeapi.co/api/v2/pokemon/?offset=0&limit=500";
		RestTemplate template = new RestTemplate();
		ResponseEntity<String> resp = template.getForEntity(apiurl, String.class);
        
		InputStream is = new ByteArrayInputStream(resp.getBody().getBytes());
		JsonReader r = Json.createReader(is);
		JsonObject obj = r.readObject();
        JsonArray array = obj.getJsonArray("results");

        //the apiurl above only have 2 elements, the name and another apiurl
        //the 2nd apiurl is the one with the details of the pokemon
		array.stream().map(v -> (JsonObject) v)
			.forEach((JsonObject v) -> {
                //2nd apiurl
                String url = v.getString("url");
                
                ResponseEntity<String> resp2 = template.getForEntity(url, String.class);
                InputStream is2 = new ByteArrayInputStream(resp2.getBody().getBytes());
                JsonReader r2 = Json.createReader(is2);
                JsonObject obj2 = r2.readObject();
                
                //testing so only take 3 details, name height and imageLink
                JsonObject pkmJson = Json.createObjectBuilder()
                    .add("name", obj2.getString("name"))
                    .add("height", obj2.getInt("height"))
                    .add("imageLink", obj2.getJsonObject("sprites").getString("front_default"))
                    .build();
                System.out.println(pkmJson);
                //save to a List because List got .range() to retrieve specific element from a index range
                redisTemplate.opsForList().rightPush("pokemons", pkmJson.toString());
			});
    }

    public List<Pokemon> retrievePokemonList(String page) 
    {
        //instantiate a new List<String> to parse in the 3 details we want from redis db
        List<String> pkmListString = new ArrayList<>();
        
        Integer pageInteger = Integer.parseInt(page);
        //page1 index range is always 0 to 9, first 10 values
        int page1StartIndex = 0;
        int page1EndIndex = 9;
        
        if (page.equals("1")) {
            //basically if its the first page, just use back index 0 to 9 (first 10 values)
            pkmListString = redisTemplate.opsForList().range("pokemons", page1StartIndex, page1EndIndex);
        } else if (!page.equals("1")) {
            //but if its page 2 and beyond, we have to +10 to the indexes so it skips the first 10 values
            //and also * the 10 with the page number, the math works like this
            //page 2 (index 0+10*(2-1) to 9+10(2-1), index 10 to 19)
            //page 3 (index 0+10*(3-1) to 9+10(3-1), index 20 to 29)
            //page 4 (index 0+10*(4-1) to 9+10(4-1), index 30 to 39)
            pkmListString = redisTemplate.opsForList()
                .range("pokemons", //List key
                        page1StartIndex+10*(pageInteger-1), 
                        page1EndIndex+10*(pageInteger-1));
        }
        
        //instantiate a List<Pokemon> to parse in the 10 pokemon to return 
        List<Pokemon> pkmListObj = new ArrayList<>();
        pkmListString.stream().forEach(s -> {
            //the string was converted from a JsonObject.toString, to reverse this process we need to use
            //new StringReader()
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
