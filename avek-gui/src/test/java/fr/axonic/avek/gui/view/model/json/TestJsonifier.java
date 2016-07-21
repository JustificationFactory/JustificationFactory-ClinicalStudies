package fr.axonic.avek.gui.view.model.json;

import fr.axonic.avek.gui.model.json.Jsonifier;
import fr.axonic.avek.gui.model.sample.ExampleState;
import fr.axonic.avek.gui.model.sample.ExampleStateBool;
import fr.axonic.avek.model.MonitoredSystem;
import fr.axonic.avek.model.base.ANumber;
import fr.axonic.avek.model.base.ARangedEnum;
import fr.axonic.avek.model.base.AString;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nathaël N on 20/07/16.
 */
public class TestJsonifier {



	@Test public void testPrimitives() {
		test("Totoo", String.class);
		test(42, Integer.class);
		test(49.3, Double.class);
		test(98765432123456789L, Long.class);
	}

	@Test public void testObjects() {
		// Array with unknown element number
		ArrayList<String> ls = new ArrayList<>();
		for(int i=(int)(100*Math.random()); i>0; i--) {
			ls.add((char) ('A' + Math.random() * ('Z' - 'A')) + "_" + Math.random());
		}

		test(ls, ArrayList.class);

		Jsonifier<ARangedEnum> jsonifier = new Jsonifier<>(ARangedEnum.class);

		ARangedEnum<ExampleState> arees = new ARangedEnum<>(ExampleState.MEDIUM);
		ARangedEnum o2 = jsonifier.fromJson(Jsonifier.toJson(arees));
		assertEquals(arees.getValue().toString(), o2.getValue().toString());
		assertEquals(arees.getRange(), o2.getRange());
		test2(arees, ARangedEnum.class);

		MonitoredSystem ms = new MonitoredSystem(42);
		ms.addCategory("Cat1");
		ms.addCategory("Cat2");
		ms.addAVar("Cat1", arees);
		ms.addAVar("Cat2", new ANumber(49.3));

		ARangedEnum<ExampleStateBool> areesb = new ARangedEnum<>(ExampleStateBool.FALSE);
		o2 = jsonifier.fromJson(Jsonifier.toJson(areesb));
		assertEquals(areesb.getValue().toString(), o2.getValue().toString());
		assertEquals(areesb.getRange(), o2.getRange());
		ms.addAVar("Cat2", areesb);
		ms.addAVar("Cat1", new AString("Some AString"));

		test2(areesb, ARangedEnum.class);
		test2(ms, MonitoredSystem.class);
	}

	private <T> void test(T o, Class<T> tClass) {
		T o2 = new Jsonifier<>(tClass).fromJson(Jsonifier.toJson(o));
		assertEquals(o, o2);
	}
	private <T> void test2(T o, Class<T> tClass) {
		Jsonifier<T> js = new Jsonifier<>(tClass);
		String oJson = Jsonifier.toJson(o);
		String o2Json = Jsonifier.toJson(js.fromJson(oJson));
		assertEquals(oJson, o2Json);
	}

}
