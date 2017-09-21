/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.autofixes.Region;
import net.sourceforge.pmd.autofixes.RegionImp;

/**
 * Tests for implementation of Region
 */
public final class RegionImpTest {

    private Region region = null;

    @Test
    public void insertZeroToOffsetShouldSucceed() {
        final int offset = 0;
        final int length = 1;

        new RegionImp(offset, length);
    }

    @Test
    public void insertZeroToLengthShouldSucceed() {
        final int offset = 1;
        final int length = 0;

        new RegionImp(offset, length);
    }

    @Test
    public void insertZeroToOffsetAndLengthShouldSucceed() {
        final int offset = 0;
        final int length = 0;

        new RegionImp(offset, length);
    }


    @Test(expected = IllegalArgumentException.class)
    public void insertNegativeOffsetShouldFail() {
        final int offsetToFail = -1;
        final int length = 1;

        new RegionImp(offsetToFail, length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertNegativeLengthShouldFail() {
        final int offset = 1;
        final int lengthToFail = -1;

        new RegionImp(offset, lengthToFail);
    }

    @Test
    public void getInsertedOffsetAndLengthShouldSucceed() {
        final int offsetExpected = 1;
        final int lengthExpected = 1;

        region = new RegionImp(offsetExpected, lengthExpected);

        Assert.assertEquals(offsetExpected, region.getOffset());
        Assert.assertEquals(lengthExpected, region.getLength());
    }

    @Test
    public void getOffsetAfterEndingShouldSucceed() {
        final int offset = 0;
        final int length = 1;

        region = new RegionImp(offset, length);

        Assert.assertEquals(offset + length, region.getOffsetAfterEnding());
    }
}
