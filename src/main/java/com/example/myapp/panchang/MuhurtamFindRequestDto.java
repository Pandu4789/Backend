package com.example.myapp.panchang;
import java.util.List;

import lombok.Data;

@Data
public class MuhurtamFindRequestDto {
    private List<String> nakshatrams;// Target nakshatrams from frontend
}
