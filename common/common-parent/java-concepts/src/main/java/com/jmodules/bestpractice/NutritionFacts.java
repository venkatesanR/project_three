package com.jmodules.bestpractice;

public class NutritionFacts {
	private final int servingSize;
	// (mL)required
	private final int servings;
	// (per container) required
	private final int calories;
	// optional
	private final int fat;
	// (g)optional
	private final int sodium;
	// (mg)optional
	private final int carbohydrate; // (g)optional

	public NutritionFacts(int servingSize, int servings) {
		this(servingSize, servings, 0);
	}

	public NutritionFacts(int servingSize, int servings, int calories) {
		this(servingSize, servings, calories, 0);
	}

	public NutritionFacts(int servingSize, int servings, int calories, int fat) {
		this(servingSize, servings, calories, fat, 0);
	}

	public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
		this(servingSize, servings, calories, fat, sodium, 0);
	}

	public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
		this.servingSize = servingSize;
		this.servings = servings;
		this.calories = calories;
		this.fat = fat;
		this.sodium = sodium;
		this.carbohydrate = carbohydrate;
	}

	// using builder
	public static class BuildNutrition implements Builder<NutritionFacts> {
		// Required parameters
		private final int servingSize;
		private final int servings;
		// Optionalparameters-
		// initialized to default
		// values
		private int calories = 0;
		private int fat = 0;
		private int carbohydrate = 0;
		private int sodium = 0;

		public BuildNutrition(int servingSize, int servings) {
			this.servingSize = servingSize;
			this.servings = servings;
		}

		public BuildNutrition calories(int val) {
			calories = val;
			return this;
		}

		public BuildNutrition fat(int val) {
			fat = val;
			return this;
		}

		public BuildNutrition carbohydrate(int val) {
			carbohydrate = val;
			return this;

		}

		public BuildNutrition sodium(int val) {
			sodium = val;
			return this;
		}

		public NutritionFacts build() {
			return new NutritionFacts(this);
		}
	}

	private NutritionFacts(BuildNutrition builder) {
		servingSize = builder.servingSize;
		servings = builder.servings;
		calories = builder.calories;
		fat = builder.fat;
		sodium = builder.sodium;
		carbohydrate = builder.carbohydrate;
	}
}
