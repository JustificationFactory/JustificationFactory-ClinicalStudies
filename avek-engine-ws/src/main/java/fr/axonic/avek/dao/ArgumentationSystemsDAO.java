package fr.axonic.avek.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.axonic.avek.engine.ArgumentationSystem;
import fr.axonic.avek.engine.ArgumentationSystemAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ArgumentationSystemsDAO {
    private static final Logger LOGGER= LoggerFactory.getLogger(ArgumentationSystemsDAO.class);

    private static final String DIR="data";

    public static Map<String, ArgumentationSystemAPI> loadArgumentationSystems() throws IOException {
        Map<String, ArgumentationSystemAPI> res=new HashMap<>();
        ObjectMapper mapper=new JerseyMapperProvider().getContext(null);
        File dir = new File(DIR);
        if(!dir.exists()){
            dir.mkdir();
        }
        if(dir.listFiles()!=null){
            for (File file:dir.listFiles()) {
                String name=file.getName().split("\\.")[0];
                LOGGER.info("Found Argumentation System "+name);
                res.put(name,mapper.readValue(file, ArgumentationSystem.class));
            }
        }
        return res;
    }

    public static void saveArgumentationSystem(String name, ArgumentationSystemAPI argumentationSystem) throws IOException {
        File file=new File(DIR+"/"+name+".data");
        file.createNewFile();
        ObjectMapper mapper=new JerseyMapperProvider().getContext(null);
        mapper.writeValue(file,argumentationSystem);


    }

    public static void removeArgumentationSystem(String name) throws IOException {
        File file=new File(DIR+"/"+name+".data");
        if(file.exists()){
            file.delete();
        }

    }
}