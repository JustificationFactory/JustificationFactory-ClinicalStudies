package fr.axonic.avek.model.base;

import fr.axonic.avek.model.verification.exception.VerificationException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by cduffau on 07/07/16.
 */
public class ANumberTest {

    @Test
    public void testValue() throws VerificationException {
        ANumber aNumber= (ANumber) AVar.create(new Format(FormatType.NUMBER));
        aNumber.setValue(1.0);
        assertEquals(aNumber.intValue(),1);
        assertEquals(aNumber.floatValue(),1.0f,0);
        assertEquals(aNumber.doubleValue(),1.0d,0);
        assertEquals(aNumber.longValue(),1L);
    }

    @Test
    public void testVerifiableProperties() throws VerificationException {
        AVar aNumber= AVar.create(new Format(FormatType.NUMBER));
        aNumber.setValue(1.0);
        assertTrue(aNumber.isPropertyVerifiable(AVarProperty.MIN.name()));
        assertTrue(aNumber.isPropertyVerifiable(AVarProperty.MAX.name()));
        assertTrue(aNumber.isPropertyVerifiable(AVarProperty.DEFAULT_VALUE.name()));
        assertTrue(aNumber.isPropertyVerifiable(AVarProperty.VALUE.name()));
    }

    @Test
    public void testClone() throws VerificationException, CloneNotSupportedException {
        AVar aNumber= AVar.create(new Format(FormatType.NUMBER));
        aNumber.setValue(1.0);
        AVar aNumber2=aNumber.clone();
        assertEquals(aNumber,aNumber2);
    }



}