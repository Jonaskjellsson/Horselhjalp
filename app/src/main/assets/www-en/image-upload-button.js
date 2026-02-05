// Image Upload Button Integration
(function() {
    'use strict';
    
    // Wait for DOM to be ready
    function initImageUploadButton() {
        // Find the header controls area
        const headerContent = document.querySelector('.max-w-4xl.mx-auto.flex.items-center.justify-between');
        if (!headerContent) {
            setTimeout(initImageUploadButton, 500);
            return;
        }
        
        // Create upload button
        const uploadButton = document.createElement('a');
        uploadButton.href = 'image-upload.html';
        uploadButton.className = 'inline-flex items-center justify-center gap-2 whitespace-nowrap text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0 shadow-sm hover:bg-accent hover:text-accent-foreground px-4 h-12 rounded-full border-4 border-foreground control-btn';
        uploadButton.style.marginLeft = '1rem';
        uploadButton.setAttribute('aria-label', 'Upload image');
        uploadButton.setAttribute('title', 'Upload image');
        
        // Add camera icon
        uploadButton.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="h-6 w-6">
                <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"></path>
                <circle cx="12" cy="13" r="3"></circle>
            </svg>
        `;
        
        // Add button next to theme toggle
        const themeButton = headerContent.querySelector('[data-testid="theme-toggle-btn"]');
        if (themeButton && themeButton.parentNode) {
            themeButton.parentNode.insertBefore(uploadButton, themeButton);
        }
    }
    
    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initImageUploadButton);
    } else {
        initImageUploadButton();
    }
})();
