package deloitte;

import java.util.Set;

public class UpdateProfileWrapElements {
	private String name;
	private Set<ProfileElements> recProfileSet;
	private Set<ProfileElements> recProfileSetRemove;

	public String getNameType() {
		return name;
	}
	public void setNameType(String name) {
		this.name = name;
	}
	public Set<ProfileElements> getProfileSet() {
		return recProfileSet;
	}
	public void setProfileSet(Set<ProfileElements> recProfileSet) {
		this.recProfileSet = recProfileSet;
	}
	public Set<ProfileElements> getProfileSetRemove() {
		return recProfileSetRemove;
	}
	public void setProfileSetRemove(Set<ProfileElements> recProfileSetRemove) {
		this.recProfileSetRemove = recProfileSetRemove; 
	}

	@Override
	public String toString() {
		return "NameType:: Name=" + this.name  + " getProfileSet=" + this.recProfileSet +" getProfileSetRemove=" + this.recProfileSetRemove ;
	}

}
