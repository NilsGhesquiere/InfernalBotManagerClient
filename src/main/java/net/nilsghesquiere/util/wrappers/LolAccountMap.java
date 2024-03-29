package net.nilsghesquiere.util.wrappers;

import java.util.HashMap;
import java.util.Map;

import net.nilsghesquiere.entities.LolAccount;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class LolAccountMap{
	private Map<String, LolAccount> map;

	public LolAccountMap() {
		this.map = new HashMap<>();
	}	
	
	@JsonAnySetter 
	public void add(String key, LolAccount value) {
		map.put(key, value);
	}

	@JsonAnyGetter
	public Map<String, LolAccount> getMap() {
		return map;
	}
	
}
