package io.github.jokoframework.uuid;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


/**
 * Created by danicricco on 2/26/18.
 */
public class UUIDGenerationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UUIDGenerationTest.class);

    @Test
    public void testUUIDLength(){
        testLength(12);
        testLength(16);
        testLength(20);
    }

    @Test
    public void testRandomness(){
        ArrayList<String> generatedUUIDs=new ArrayList<String>();
        TXUUIDGenerator generator=new TXUUIDGenerator(20);
        long timeToRunInSeconds=30;
        long startTime = System.currentTimeMillis();

        LOGGER.info("Running collision detection test for " +
                ""+timeToRunInSeconds+"sec. ");
        while(System.currentTimeMillis()-startTime<=timeToRunInSeconds*1000){
            String uuid = generator.generate();
            if(generatedUUIDs.contains(uuid)){
                Assert.fail("Colision detected within " +
                        ""+timeToRunInSeconds+" seconds. UUID "+uuid+" was " +
                        "previously generated");
            }
        }
    }

    /***
     * Genera un id y comprueba que sea del tamaño esperado
     * @param pExpectedLength
     */
    private void testLength(int pExpectedLength) {
        TXUUIDGenerator generator=new TXUUIDGenerator(pExpectedLength);
        String uuid = generator.generate();
        Assert.assertEquals(pExpectedLength,uuid.length());
    }
}
