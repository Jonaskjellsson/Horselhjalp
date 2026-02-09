package se.jonas.horselhjalp

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    
    @Test
    fun testNewlineRemovalFromRecognizedText() {
        // Test that newlines are removed from speech recognition results
        val input = "Ja då provar vi igen\ndenna gång om det \nblir bättre"
        val expected = "Ja då provar vi igen denna gång om det blir bättre"
        
        // Simulate the cleaning process
        val newlineRegex = Regex("\n+")
        val multipleSpacesRegex = Regex(" +")
        val result = input
            .replace(newlineRegex, " ")
            .replace(multipleSpacesRegex, " ")
            .trim()
        
        assertEquals(expected, result)
    }
    
    @Test
    fun testSessionSeparatorPreserved() {
        // Test that session separators (3 newlines) are preserved
        val sessionSeparator = "\n\n\n"
        val input = "First session${sessionSeparator}Second session"
        
        // The MULTIPLE_NEWLINES_REGEX should NOT replace exactly 3 newlines
        val multipleNewlinesRegex = Regex("\n{4,}")  // Only matches 4+ newlines
        val result = input.replace(multipleNewlinesRegex, sessionSeparator)
        
        assertEquals(input, result)  // Should remain unchanged
    }
    
    @Test
    fun testExcessiveNewlinesReduced() {
        // Test that 4+ newlines are reduced to 3 (2 empty lines)
        val sessionSeparator = "\n\n\n"
        val input = "Text\n\n\n\n\nMore text"  // 5 newlines
        
        val multipleNewlinesRegex = Regex("\n{4,}")
        val result = input.replace(multipleNewlinesRegex, sessionSeparator)
        
        assertEquals("Text${sessionSeparator}More text", result)
    }
}