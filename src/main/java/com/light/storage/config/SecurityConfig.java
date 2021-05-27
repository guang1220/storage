package com.light.storage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.light.storage.entity.Admin;
import com.light.storage.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AdminService adminService;

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;

    //设置记住我，免登录
//    @Bean
//    public PersistentTokenRepository persistentTokenRepository(){
//        JdbcTokenRepositoryImpl jdbcTokenRepository=new JdbcTokenRepositoryImpl();
//        jdbcTokenRepository.setDataSource(dataSource);
////        jdbcTokenRepository.setCreateTableOnStartup(true);
//        return jdbcTokenRepository;
//    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(adminService);
    }

    /*
    * BCryptPasswordEncoder相关知识：
    用户表的密码通常使用MD5等不可逆算法加密后存储，为防止彩虹表破解更会先使用一个特定的字符串（如域名）加密，然后再使用一个随机的salt（盐值）加密。
    特定字符串是程序代码中固定的，salt是每个密码单独随机，一般给用户表加一个字段单独存储，比较麻烦。
    BCrypt算法将salt随机并混入最终加密后的密码，验证时也无需单独提供之前的salt，从而无需单独处理salt问题。
    * */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //静态页面放行
    @Override
    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/static/css/**", "/static/js/**", "/index.html", "/static/img/**", "/static/fonts/**", "/favicon.ico");
        web.ignoring().antMatchers("/static/**", "/index.html");
    }


    @Bean
    LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    System.out.println(authentication.getPrincipal());
                    Admin admin = (Admin) authentication.getPrincipal();
                    admin.setPassword(null);
                    //将对象转换为json
                    String res = new ObjectMapper().writeValueAsString(admin);
                    out.write(res);
                    out.flush();
                    out.close();
                }
        );
        loginFilter.setAuthenticationFailureHandler((request, response, exception) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write("0");
                    out.flush();
                    out.close();
                }
        );
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        //拦截这个请求进行认证
        loginFilter.setFilterProcessesUrl("/doLogin");

        return loginFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
         * 实际中所有请求一般先 authorizeRequests()
         * 处理放行页面 permitAll
         * 处理特殊权限页面 hasAuth
         * 剩余全部需登录 authenticated()
         *
         * */
        http.logout().logoutUrl("/logout").logoutSuccessHandler((req, resp, authentication) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("注销成功!");
                    out.flush();
                    out.close();
                    //清空redis缓存
//                    Set<String> keys = stringRedisTemplate.keys("*");
//                    Iterator<String> it1 = keys.iterator();
//                    while (it1.hasNext()) {
//                        stringRedisTemplate.delete(it1.next());
//                    }
                }
        ).deleteCookies("JSESSIONID").permitAll()
                //就算配置了loginFilter，也得在这里permitAll
                .and().authorizeRequests().antMatchers("/doLogin", "/sign", "/findPass", "/setPass").permitAll().anyRequest().authenticated()
                .and()
                .csrf().disable()//如果这个不关，则登录的Post会被拦截，需要crsf认证，开了的话，logout请求则必须为Post，这样可以防止别人调用logout的get请求退出。
//                .cors().and().csrf().disable().antMatcher("/")//跨域解决,无效，配置这个后所有请求不用登录也能访问
//                没有认证时，在这里处理结果，不要重定向,认证异常入口点
                .exceptionHandling().authenticationEntryPoint((req, resp, authException) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(401);
                    PrintWriter out = resp.getWriter();
                    out.write("认证异常");
                    out.flush();
                    out.close();
                }
        );
    }
}
