package com.arthursouza.todosimple.models.enums;


import java.util.Objects;

import org.springframework.context.annotation.Profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProfileEnum {
    
    ADMIN(1, "ROLE_ADMIN"),
    USER(2, "ROLE_USER");

    
    private int code;
    private String description;
    

    //Returns one profileEnum (ADMIN, USER)
    public static ProfileEnum toEnum(Integer code){

        if (Objects.isNull(code)) 
            return null;

        for (ProfileEnum x : ProfileEnum.values()) {

            if (code.equals(x.getCode())) 
                return x;

        }
    
        throw new IllegalArgumentException("Invalid code: "+code);
        
        
    }

}
