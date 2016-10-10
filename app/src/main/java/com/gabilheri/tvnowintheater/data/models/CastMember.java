package com.gabilheri.tvnowintheater.data.models;

import com.squareup.moshi.Json;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/9/16.
 */

public class CastMember {

    private int id;
    private String character;
    private String name;
    private int order;

    @Json(name = "cast_id")
    private int castId;

    @Json(name = "credit_id")
    private String creditId;

    @Json(name = "profile_path")
    private String profilePath;

    public CastMember() {
    }

    public int getId() {
        return id;
    }

    public CastMember setId(int id) {
        this.id = id;
        return this;
    }

    public int getCastId() {
        return castId;
    }

    public CastMember setCastId(int castId) {
        this.castId = castId;
        return this;
    }

    public String getCharacter() {
        return character;
    }

    public CastMember setCharacter(String character) {
        this.character = character;
        return this;
    }

    public String getCreditId() {
        return creditId;
    }

    public CastMember setCreditId(String creditId) {
        this.creditId = creditId;
        return this;
    }

    public String getName() {
        return name;
    }

    public CastMember setName(String name) {
        this.name = name;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public CastMember setOrder(int order) {
        this.order = order;
        return this;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public CastMember setProfilePath(String profilePath) {
        this.profilePath = profilePath;
        return this;
    }
}
