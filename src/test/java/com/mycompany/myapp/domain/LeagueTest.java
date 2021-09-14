package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LeagueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(League.class);
        League league1 = new League();
        league1.setId(1L);
        League league2 = new League();
        league2.setId(league1.getId());
        assertThat(league1).isEqualTo(league2);
        league2.setId(2L);
        assertThat(league1).isNotEqualTo(league2);
        league1.setId(null);
        assertThat(league1).isNotEqualTo(league2);
    }
}
