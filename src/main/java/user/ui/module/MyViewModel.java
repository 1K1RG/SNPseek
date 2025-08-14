package user.ui.module;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zkmax.zul.GoldenLayout;
import org.zkoss.zkmax.zul.GoldenPanel;

public class MyViewModel {

	private String _areas = "A B C\nA B C\nA B C\nD D D\nD D D";

	public String getAreas() {
		return _areas;
	}

	public void setAreas(String areas) {
		_areas = areas;
	}

	@Command
	public void addPanel(@BindingParam("gl") GoldenLayout gl) {
		gl.appendChild(new GoldenPanel());
	}
}