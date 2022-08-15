package uwu.flauxy.cape;

import lombok.Getter;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;

public class CapeManager {
    @Getter
    public int index;
    @Getter
    public static CapeManager INSTANCE;
    @Getter
    public ArrayList<Cape> capes = new ArrayList<>();

    public CapeManager(){
        final Reflections reflections = new Reflections("uwu.flauxy.cape.impl");
        final Set<Class<? extends Cape>> classes = reflections.getSubTypesOf(Cape.class);
        for (Class<?> aClass : classes) {
            try {
                final Cape mod = (Cape) aClass.newInstance();
                capes.add(mod);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }



}
