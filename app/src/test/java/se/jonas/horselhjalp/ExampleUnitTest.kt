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
        // This matches the behavior in MainActivity.onResults() and onPartialResults()
        val input = "Ja då provar vi igen\ndenna gång om det \nblir bättre"
        val expected = "Ja då provar vi igen denna gång om det blir bättre"
        
        // Simulate the cleaning process used in MainActivity
        // Note: In production, these regex patterns are defined as constants
        val newlineRegex = Regex("\n+")  // NEWLINE_REGEX
        val multipleSpacesRegex = Regex(" +")  // MULTIPLE_SPACES_REGEX
        val result = input
            .replace(newlineRegex, " ")
            .replace(multipleSpacesRegex, " ")
            .trim()
        
        assertEquals(expected, result)
    }
    
    @Test
    fun testSessionSeparatorPreserved() {
        // Test that session separators (2 newlines = 1 empty line) are preserved
        val sessionSeparator = "\n\n"
        val input = "First session${sessionSeparator}Second session"
        
        // The MULTIPLE_NEWLINES_REGEX should NOT replace exactly 2 newlines
        val multipleNewlinesRegex = Regex("\n{3,}")  // Only matches 3+ newlines
        val result = input.replace(multipleNewlinesRegex, sessionSeparator)
        
        assertEquals(input, result)  // Should remain unchanged
    }
    
    @Test
    fun testExcessiveNewlinesReduced() {
        // Test that 3+ newlines are reduced to 2 (1 empty line)
        // This cleaning happens in the post-processing step in MainActivity.onResults()
        val sessionSeparator = "\n\n"
        val input = "Text\n\n\n\nMore text"  // 4 newlines
        
        // MULTIPLE_NEWLINES_REGEX in MainActivity handles this edge case
        val multipleNewlinesRegex = Regex("\n{3,}")
        val result = input.replace(multipleNewlinesRegex, sessionSeparator)
        
        assertEquals("Text${sessionSeparator}More text", result)
    }
}