package com.comet.letseat.module

import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseInputValidator
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseViewValidator
import com.comet.letseat.map.view.dialog.choose.valid.predict.PredictValidator
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    @Singleton
    fun provideChooseValidator() : ChooseInputValidator {
        return ChooseInputValidator()
    }

    @Provides
    @Singleton
    fun provideChooseInputViewValidator(validator: ChooseInputValidator) : ChooseViewValidator {
        return ChooseViewValidator(validator)
    }


    @Provides
    @Singleton
    fun providePredictValidator() : PredictValidator {
        return PredictValidator()
    }

    @Provides
    @Singleton
    fun provideResultValidator() : ResultValidator {
        return ResultValidator()
    }


}