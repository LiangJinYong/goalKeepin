package goalKeepin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("goalKeepin.data")
public class RepoConfiguration {
	
}
