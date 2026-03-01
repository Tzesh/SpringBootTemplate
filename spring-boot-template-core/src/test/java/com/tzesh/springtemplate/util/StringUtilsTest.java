package com.tzesh.springtemplate.util;

import com.tzesh.springtemplate.base.util.StringUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {
    @Test
    void testIsNullOrEmpty_withNull() {
        assertTrue(StringUtils.isNullOrEmpty(null));
    }

    @Test
    void testIsNullOrEmpty_withEmpty() {
        assertTrue(StringUtils.isNullOrEmpty(""));
    }

    @Test
    void testIsNullOrEmpty_withNonEmpty() {
        assertFalse(StringUtils.isNullOrEmpty("test"));
    }
}

