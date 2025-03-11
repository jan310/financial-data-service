package jan.ondra.financialdataservice;

import org.springframework.boot.SpringApplication;

public class TestFinancialDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(FinancialDataServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
