package com.gabilheri.tvnowintheater.data.models;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/9/16.
 */

public class Genre {

    private int id;
    private String name;

    public Genre() {
    }

    public int getId() {
        return id;
    }

    public Genre setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Genre setName(String name) {
        this.name = name;
        return this;
    }
}
