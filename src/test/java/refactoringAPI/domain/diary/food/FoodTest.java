package refactoringAPI.domain.diary.food;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.annotation.Profile;
import refactoringAPI.domain.diary.EntityId;
import refactoringAPI.domain.diary.diabetesDiary.DiabetesDiary;
import refactoringAPI.domain.diary.diet.Diet;
import refactoringAPI.domain.diary.diet.EatTime;
import refactoringAPI.domain.diary.writer.Role;
import refactoringAPI.domain.diary.writer.Writer;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Profile("test")
public class FoodTest {
    //예외 캐치용 객체
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    Writer writer;
    DiabetesDiary diary;
    Diet diet;
    Food food;

    @Before
    public void setUp() {
        writer = new Writer(EntityId.of(Writer.class, 1L), "me", "test@naver.com", Role.Admin);
        diary = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 1L), writer, 100, "test", LocalDateTime.now());
        writer.addDiary(diary);
        diet = new Diet(EntityId.of(Diet.class, 1L), diary, EatTime.Lunch, 110);
        diary.addDiet(diet);
        food = new Food(EntityId.of(Food.class, 1L), diet, "Pizza");
        diet.addFood(food);
    }

    @Test
    public void makeRelationWithDiet() {
        assertThat(food.getDiet()).isEqualTo(diet);
    }

    @Test
    public void createInvalidFoodName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("food name length should be between 1 and 50");
        StringBuilder foodName = new StringBuilder();
        IntStream.range(0, 100).forEach(i -> foodName.append("a"));
        new Food(EntityId.of(Food.class, 2L), diet, foodName.toString());
    }

    @Test
    public void update() {
        food.update("chicken");
        assertThat(food.getId()).isEqualTo(1L);
        assertThat(food.getFoodName()).isEqualTo("chicken");
    }

    @Test
    public void updateInvalidFoodName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("food name length should be between 1 and 50");
        food.update("");
    }
}