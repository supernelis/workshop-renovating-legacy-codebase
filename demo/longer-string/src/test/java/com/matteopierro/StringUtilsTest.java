package com.matteopierro;

import org.approvaltests.Approvals;
import org.approvaltests.combinations.CombinationApprovals;
import org.junit.Test;

import static com.matteopierro.StringUtils.longer;

public class StringUtilsTest {

    @Test
    public void longerGoldenMaster() throws Exception {

    }

    private void verifyAllCombinations(String[] strings) throws Exception {
        CombinationApprovals.verifyAllCombinations(
                StringUtils::longer,
                strings,
                strings);
    }
}
