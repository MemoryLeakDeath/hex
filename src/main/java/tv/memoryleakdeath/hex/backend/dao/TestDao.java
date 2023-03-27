package tv.memoryleakdeath.hex.backend.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TestDao {
	private static final Logger logger = LoggerFactory.getLogger(TestDao.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean testDatabase() {
		String sql = "select 1";
		Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
		logger.info("Database test returned: {}", String.valueOf(result));
		return result.equals(1);
	}
}
