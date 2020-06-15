package io.newify.processor

import io.mockk.mockk

inline fun <reified T : Any> mock(): T = mockk(relaxed = true, relaxUnitFun = true)