package com.example.cofee_shop.data.repositories

import com.example.cofee_shop.core.ApiResult
import com.example.cofee_shop.data.local.database.dao.CoffeeDao
import com.example.cofee_shop.data.local.database.entities.CoffeeEntity
import com.example.cofee_shop.data.mappers.CoffeeMapper
import com.example.cofee_shop.data.remote.api.CoffeeApiService
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.repositories.CoffeeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class CoffeeRepositoryImpl @Inject constructor(
    private val apiService: CoffeeApiService
    , private val coffeeDao: CoffeeDao
) : CoffeeRepository {

    private var cachedHotCoffees: List<Coffee> = emptyList()
    private var cachedIcedCoffees: List<Coffee> = emptyList()
    private var lastHotCoffeesFetchTime: Long = 0
    private var lastIcedCoffeesFetchTime: Long = 0

    companion object {
        private const val CACHE_DURATION = 5 * 60 * 1000L // 5 minutes
    }

    override suspend fun getHotCoffees(): ApiResult<List<Coffee>> {
        return try {
            // Check if cache is still valid
            if (cachedHotCoffees.isNotEmpty() &&
                System.currentTimeMillis() - lastHotCoffeesFetchTime < CACHE_DURATION) {
                return ApiResult.Success(cachedHotCoffees)
            }

            val hotCoffeesDto = apiService.getHotCoffees()
            val hotCoffees = CoffeeMapper.mapHotCoffeeListToCoffeeList(hotCoffeesDto)

            // Update cache
            cachedHotCoffees = hotCoffees
            lastHotCoffeesFetchTime = System.currentTimeMillis()

            ApiResult.Success(hotCoffees)
        } catch (e: Exception) {
            // If network fails but we have cached data, return cached data
            if (cachedHotCoffees.isNotEmpty()) {
                ApiResult.Success(cachedHotCoffees)
            } else {
                ApiResult.Failure(e)
            }
        }
    }

    override suspend fun getIcedCoffees(): ApiResult<List<Coffee>> {
        return try {
            if (cachedIcedCoffees.isNotEmpty() &&
                System.currentTimeMillis() - lastIcedCoffeesFetchTime < CACHE_DURATION) {
                return ApiResult.Success(cachedIcedCoffees)
            }

            val icedCoffeesDto = apiService.getIcedCoffees()
            val icedCoffees = CoffeeMapper.mapIcedCoffeeListToCoffeeList(icedCoffeesDto)

            cachedIcedCoffees = icedCoffees
            lastIcedCoffeesFetchTime = System.currentTimeMillis()

            ApiResult.Success(icedCoffees)
        } catch (e: Exception) {
            if (cachedIcedCoffees.isNotEmpty()) {
                ApiResult.Success(cachedIcedCoffees)
            } else {
                ApiResult.Failure(e)
            }
        }
    }

    override suspend fun getCoffeeById(id: Int, isHot: Boolean): ApiResult<Coffee?> {
        return try {
            val coffeesResult = if (isHot) {
                getHotCoffees()
            } else {
                getIcedCoffees()
            }

            when (coffeesResult) {
                is ApiResult.Success -> {
                    val coffee = coffeesResult.data.find { it.id == id }
                    ApiResult.Success(coffee)
                }
                is ApiResult.Failure -> coffeesResult
                is ApiResult.Loading -> ApiResult.Loading
            }
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    override suspend fun searchCoffee(query: String): ApiResult<List<Coffee>> {
        return try {
            val hotCoffeesResult = getHotCoffees()
            val icedCoffeesResult = getIcedCoffees()

            val hotCoffees = when (hotCoffeesResult) {
                is ApiResult.Success -> hotCoffeesResult.data
                else -> emptyList()
            }

            val icedCoffees = when (icedCoffeesResult) {
                is ApiResult.Success -> icedCoffeesResult.data
                else -> emptyList()
            }

            val allCoffees = hotCoffees + icedCoffees

            val filteredCoffees = allCoffees.filter { coffee ->
                coffee.title.contains(query, ignoreCase = true) ||
                        coffee.description.contains(query, ignoreCase = true) ||
                        coffee.ingredients.any { ingredient ->
                            ingredient.contains(query, ignoreCase = true)
                        }
            }

            ApiResult.Success(filteredCoffees)
        } catch (e: Exception) {
            ApiResult.Failure(e)
        }
    }

    override suspend fun getDrinksByType(isHot: Boolean): Flow<List<CoffeeEntity>> {
        return coffeeDao.getDrinksByType(isHot)
    }

    override suspend fun getAllDrinks(): Flow<List<CoffeeEntity>> {
        return coffeeDao.getAllDrinks()
    }

    override suspend fun getDrinkById(drinkId: Int): CoffeeEntity? {
        return coffeeDao.getDrinkById(drinkId)
    }

    override suspend fun searchDrinks(query: String): Flow<List<CoffeeEntity>> {
        return coffeeDao.searchDrinks(query)
    }

    override suspend fun insertDrinks(drinks: List<CoffeeEntity>) {
        coffeeDao.insertDrinks(drinks)
    }

    override suspend fun clearAll() {
        coffeeDao.clearAll()
    }

    override suspend fun getCount(): Int {
        return coffeeDao.getCount()
    }
}