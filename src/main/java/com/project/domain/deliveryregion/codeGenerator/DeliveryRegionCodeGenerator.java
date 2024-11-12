package com.project.domain.deliveryregion.codeGenerator;

import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor
public class DeliveryRegionCodeGenerator {

    public String generateDeliveryCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        // 영문자 3자리 생성
        for (int i = 0; i < 3; i++) {
            char letter = (char) (random.nextInt(26) + 'A'); // A-Z 범위의 랜덤 문자
            code.append(letter);
        }

        // 숫자 4자리 생성
        for (int i = 0; i < 4; i++) {
            int number = random.nextInt(10); // 0-9 범위의 랜덤 숫자
            code.append(number);
        }

        return code.toString();
    }
}
