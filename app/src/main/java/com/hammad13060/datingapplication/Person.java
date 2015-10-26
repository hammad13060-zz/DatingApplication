package com.hammad13060.datingapplication;

/**
 * Created by Hammad on 26-10-2015.
 */
public class Person {

    int _id;
    String _user_id;
    String _name;
    int _age;
    boolean _gender;
    String _url;

    public Person() {

    }

    // constructor
    public Person(String user_id, String name, boolean gender, int age, String url) {
        this._user_id = user_id;
        this._name = name;
        this._age = age;
        this._gender = gender;
        this._url = url;
    }

    public int get_id() {return _id;}

    public String get_user_id() {
        return _user_id;
    }

    public String get_name() {
        return _name;
    }

    public int get_age() {
        return _age;
    }

    public boolean is_gender() {
        return _gender;
    }

    public String get_url() {
        return _url;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_user_id(String _user_id) {
        this._user_id = _user_id;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_age(int _age) {
        this._age = _age;
    }

    public void set_gender(boolean _gender) {
        this._gender = _gender;
    }

    public void set_url(String _url) {
        this._url = _url;
    }


    public String toString() {
        return "name: " + _name + " gender: " + _gender + " user_id: " + _user_id;
    }
}
