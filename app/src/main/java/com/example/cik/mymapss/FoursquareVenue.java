package com.example.cik.mymapss;

public class FoursquareVenue {
	private String name;
	private String city;

    private String latitude;
    private String longtitude;

	private String category;

	public FoursquareVenue() {
		this.name = "";
		this.city = "";
		this.latitude="";
		this.longtitude="";
		this.setCategory("");
	}

	public String getCity() {
		if (city.length() > 0) {
			return city;
		}
		return city;
	}

	public void setCity(String city) {
		if (city != null) {
			this.city = city.replaceAll("\\(", "").replaceAll("\\)", "");

		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }
}
