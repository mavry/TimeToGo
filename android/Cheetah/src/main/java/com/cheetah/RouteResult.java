package com.cheetah;

public class RouteResult {

	private final String routeName;
	private final int seconds;

	public RouteResult(String routeName, int seconds) {
		this.routeName = routeName;
		this.seconds = seconds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((routeName == null) ? 0 : routeName.hashCode());
		result = prime * result + seconds;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteResult other = (RouteResult) obj;
		if (routeName == null) {
			if (other.routeName != null)
				return false;
		} else if (!routeName.equals(other.routeName))
			return false;
		if (seconds != other.seconds)
			return false;
		return true;
	}
	
	

}
