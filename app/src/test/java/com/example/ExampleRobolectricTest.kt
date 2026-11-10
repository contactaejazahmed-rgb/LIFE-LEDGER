package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.entity.NotebookEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("EduBooks", appName)
  }

  @Test
  fun `notebook entity wholesale margin calculation`() {
    val notebook = NotebookEntity(
      sku = "NB-TEST-01",
      name = "Single-Line 172p",
      rulingType = "Single-line",
      size = "Regular",
      pageCount = 172,
      bindingType = "Center Stapled",
      paperGsm = 64,
      wholesalePrice = 28.0,
      mrpPrice = 45.0,
      unitsPerCarton = 60,
      currentStock = 500,
      minStockThreshold = 100
    )
    val margin = notebook.mrpPrice - notebook.wholesalePrice
    assertTrue(margin > 0)
    assertEquals(17.0, margin, 0.01)
  }
}
