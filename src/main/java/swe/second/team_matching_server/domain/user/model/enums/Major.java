package swe.second.team_matching_server.domain.user.model.enums;

public enum Major {
    PUBLIC_ADMINISTRATION("행정학과"),
    INTERNATIONAL_RELATIONS("국제관계학과"),
    ECONOMICS("경제학부"),
    SOCIAL_WELFARE("사회복지학과"),
    TAXATION("세무학과"),
    BUSINESS_ADMINISTRATION("경영학부"),
    ELECTRICAL_AND_COMPUTER_ENGINEERING("전자전기컴퓨터공학부"),
    COMPUTER_SCIENCE("컴퓨터과학부"),
    CHEMICAL_ENGINEERING("화학공학과"),
    MECHANICAL_AND_INFORMATION_ENGINEERING("기계정보공학과"),
    MATERIALS_SCIENCE_AND_ENGINEERING("신소재공학과"),
    CIVIL_ENGINEERING("토목공학과"),
    ARTIFICIAL_INTELLIGENCE("인공지능학과"),
    ENGLISH_LANGUAGE_AND_LITERATURE("영어영문학과"),
    KOREAN_LANGUAGE_AND_LITERATURE("국어국문학과"),
    KOREAN_HISTORY("국사학과"),
    PHILOSOPHY("철학과"),
    CHINESE_LANGUAGE_AND_CULTURE("중국어문화학과"),
    MATHEMATICS("수학과"),
    STATISTICS("통계학과"),
    PHYSICS("물리학과"),
    LIFE_SCIENCE("생명과학과"),
    ENVIRONMENTAL_HORTICULTURE("환경원예학과"),
    APPLIED_CHEMISTRY("융합응용화학과"),
    ARCHITECTURAL_ENGINEERING("건축학부(건축공학)"),
    ARCHITECTURE("건축학부(건축학)"),
    URBAN_ENGINEERING("도시공학과"),
    TRANSPORTATION_ENGINEERING("교통공학과"),
    LANDSCAPE_ARCHITECTURE("조경학과"),
    URBAN_ADMINISTRATION("도시행정학과"),
    URBAN_SOCIOLOGY("도시사회학과"),
    GEOINFORMATICS("공간정보공학과"),
    ENVIRONMENTAL_ENGINEERING("환경공학부"),
    MUSIC("음악학과"),
    DESIGN("디자인학과"),
    SCULPTURE("조각학과"),
    SPORTS_SCIENCE("스포츠과학과"),
    LIBERAL_STUDIES("자유전공학부"),
    CONVERGENCE_STUDIES("융합전공학부");

    private final String koreanName;

    Major(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public static Major fromKoreanName(String koreanName) {
        for (Major major : Major.values()) {
            if (major.getKoreanName().equals(koreanName)) {
                return major;
            }
        }
        throw new IllegalArgumentException("해당하는 학과를 찾을 수 없습니다: " + koreanName);
    }
}
