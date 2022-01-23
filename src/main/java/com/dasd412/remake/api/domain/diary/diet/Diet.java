/*
 * @(#)Diet.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.diet;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.food.Food;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 식단 엔티티. 일지의 하위 엔티티
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@Entity
@Table(name = "Diet", uniqueConstraints = @UniqueConstraint(columnNames = {"diet_id"}))
@IdClass(DietId.class)
public class Diet {

    /**
     * 식별 관계이므로 복합키 사용
     * 복합키의 경우 @GeneratedValue 사용 불가.
     */
    @Id
    @Column(name = "diet_id", columnDefinition = "bigint default 0")
    private Long dietId;

    /**
     * 혈당일지 "일"에 대해 "다"이므로 연관관계의 주인(외래키 관리자)이다. 되도록이면 모든 연관 관계를 지연로딩으로 사용하는 것이 성능에 좋다.
     * referencedColumnName 를 지정해줘야 순서가 거꾸로 안나온다.
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "writer_id", referencedColumnName = "writer_id"),
            @JoinColumn(name = "diary_id", referencedColumnName = "diary_id")
    })
    private DiabetesDiary diary;


    /**
     * 식사 시간
     */
    @Enumerated(EnumType.STRING)
    private EatTime eatTime;

    /**
     * 식사 혈당
     */
    private int bloodSugar;

    /**
     * 양방향 연관 관계를 맺을 때는 외래키를 갖고 있는 쪽이 연관 관계의 주인이 되어야 한다.
     * db의 1 대 다 관계에서는 '다' 쪽이 외래키를 갖고 있다. 따라서 '다'에 해당하는 Food 가 연관 관계의 주인이 되어야 한다.
     * '일'에 해당하는 Diet 는 주인이 아니므로 mappedBy 속성을 사용하여 주인이 아님을 지정한다.
     */
    @OneToMany(mappedBy = "diet", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Food> foodList = new HashSet<>();

    public Diet() {
    }

    public Diet(EntityId<Diet, Long> dietEntityId, DiabetesDiary diary, EatTime eatTime, int bloodSugar) {
        checkArgument(bloodSugar >= 0 && bloodSugar <= 1000, "bloodSugar must be between 0 and 1000");
        this.dietId = dietEntityId.getId();
        this.diary = diary;
        this.eatTime = eatTime;
        this.bloodSugar = bloodSugar;
    }

    public Long getDietId() {
        return dietId;
    }

    public EatTime getEatTime() {
        return eatTime;
    }

    private void modifyEatTime(EatTime eatTime) {
        this.eatTime = eatTime;
    }

    public int getBloodSugar() {
        return bloodSugar;
    }

    private void modifyBloodSugar(int bloodSugar) {
        checkArgument(bloodSugar >= 0 && bloodSugar <= 1000, "bloodSugar must be between 0 and 1000");
        this.bloodSugar = bloodSugar;
    }

    public List<Food> getFoodList() {
        return new ArrayList<>(foodList);
    }

    public void addFood(Food food) {
        this.foodList.add(food);
        //무한 루프에 빠지지 않도록 체크
        if (food.getDiet() != this) {
            food.makeRelationWithDiet(this);
        }
    }

    public DiabetesDiary getDiary() {
        return diary;
    }

    /**
     * 연관 관계 편의 메소드.
     * 복합키와 관련된 메서드이므로 엔티티 관계 설정이후엔 호출하면 안된다.
     *
     * @param diary 연관 관계를 맺을 혈당 일지
     */
    public void makeRelationWithDiary(DiabetesDiary diary) {
        //무한 루프 체크
        this.diary = diary;
        if (!diary.getDietList().contains(this)) {
            diary.getDietList().add(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", dietId)
                .append("diary", diary)
                .append("eatTime", eatTime)
                .append("blood sugar", bloodSugar)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(diary, dietId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Diet target = (Diet) obj;
        return Objects.equals(this.dietId, target.dietId) && Objects.equals(this.diary, target.diary);
    }

    public void update(EatTime eatTime, int bloodSugar) {
        modifyEatTime(eatTime);
        modifyBloodSugar(bloodSugar);
    }

    /**
     * 연관 관계 제거 시에만 사용
     */
    public void removeFood(Food food) {
        checkArgument(this.foodList.contains(food), "this diet dose not have the food");
        this.foodList.remove(food);
    }
}
