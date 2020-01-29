package mindustry.systems;

import arc.func.*;
import arc.struct.*;

public class EventSystem{
    private ObjectMap<Object, Array<Cons<?>>> events = new ObjectMap<>();

    public <T> Cons<T> on(Class<T> type, Cons<T> listener){
        events.getOr(type, Array::new).add(listener);
        return listener;
    }

    public <T> Cons<T> on(T type, Runnable listener){
        Cons<T> cons = e -> listener.run();
        events.getOr(type, Array::new).add(cons);
        return cons;
    }

    public <T> void fire(T type){
        fire(type.getClass(), type);
    }

    public <T> void fire(Class<?> ctype, T type){
        if(events.get(type) != null) events.get(type).each(e -> ((Cons<T>)e).get(type));
        if(events.get(ctype) != null) events.get(ctype).each(e -> ((Cons<T>)e).get(type));
    }

    public void remove(Object key, Cons listener){
        Array<Cons<?>> arr = events.get(key);
        if(arr != null) arr.remove(listener);
    }

    public void clear(){
        events.clear();
    }
}
