package me.ehp246;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import me.ehp246.AllTestSuite.EmbeddedTestSuite;
import me.ehp246.AllTestSuite.UnitTestSuite;

/**
 * @author Lei Yang
 *
 */
@Suite
@SuiteDisplayName("me.ehp246")
@SelectClasses({ EmbeddedTestSuite.class, UnitTestSuite.class })
class AllTestSuite {
    @Suite
    @SuiteDisplayName("me.ehp246.test.embedded")
    @SelectPackages("me.ehp246.test.embedded")
    static class EmbeddedTestSuite {

    }

    @Suite
    @SuiteDisplayName("me.ehp246.aufrest")
    @SelectPackages("me.ehp246.aufrest")
    static class UnitTestSuite {
    }
}
