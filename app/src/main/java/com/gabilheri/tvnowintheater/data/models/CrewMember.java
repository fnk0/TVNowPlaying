package com.gabilheri.tvnowintheater.data.models;

import com.squareup.moshi.Json;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/10/16.
 */

public class CrewMember {

    int id;
    String job;
    String name;
    String department;

    @Json(name = "profile_path")
    String profilePath;

    public CrewMember() {
    }

    public int getId() {
        return id;
    }

    public CrewMember setId(int id) {
        this.id = id;
        return this;
    }

    public String getJob() {
        return job;
    }

    public CrewMember setJob(String job) {
        this.job = job;
        return this;
    }

    public String getName() {
        return name;
    }

    public CrewMember setName(String name) {
        this.name = name;
        return this;
    }

    public String getDepartment() {
        return department;
    }

    public CrewMember setDepartment(String department) {
        this.department = department;
        return this;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public CrewMember setProfilePath(String profilePath) {
        this.profilePath = profilePath;
        return this;
    }
}
