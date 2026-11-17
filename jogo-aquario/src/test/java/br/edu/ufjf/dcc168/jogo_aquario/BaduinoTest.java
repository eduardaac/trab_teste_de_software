package br.edu.ufjf.dcc168.jogo_aquario;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({ TestSetFunc.class, TestSetEstr.class })
public class BaduinoTest {
    private BaduinoTest() {
    }
}
