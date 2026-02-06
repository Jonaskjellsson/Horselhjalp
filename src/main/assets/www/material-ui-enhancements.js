/**
 * Material 3 UI Enhancements
 * Adds copy button, progress indicator, and other Material You features
 */

(function() {
    'use strict';
    
    // Wait for DOM to be fully loaded
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
    
    function init() {
        addCopyButton();
        setupProgressIndicator();
        observeMicrophoneState();
    }
    
    /**
     * Add a copy button next to the clear button
     */
    function addCopyButton() {
        const clearBtn = document.querySelector('[data-testid="clear-btn"]');
        if (!clearBtn) {
            console.log('Clear button not found, retrying...');
            setTimeout(addCopyButton, 500);
            return;
        }
        
        // Check if copy button already exists
        if (document.querySelector('[data-testid="copy-btn"]')) {
            return;
        }
        
        // Create copy button with Material 3 styling
        const copyBtn = document.createElement('button');
        copyBtn.className = 'inline-flex items-center justify-center gap-2 whitespace-nowrap transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50 action-button';
        copyBtn.setAttribute('data-testid', 'copy-btn');
        copyBtn.setAttribute('aria-label', 'Kopiera text');
        copyBtn.disabled = true;
        
        // Create SVG icon for copy
        copyBtn.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <rect width="14" height="14" x="8" y="8" rx="2" ry="2"></rect>
                <path d="M4 16c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2h10c1.1 0 2 .9 2 2"></path>
            </svg>
            <span>KOPIERA</span>
        `;
        
        // Add click handler
        copyBtn.addEventListener('click', handleCopy);
        
        // Insert copy button before clear button
        clearBtn.parentNode.insertBefore(copyBtn, clearBtn);
        
        // Enable/disable based on text content
        const textArea = document.querySelector('[data-testid="text-display-area"]');
        if (textArea) {
            const observer = new MutationObserver(() => {
                const text = textArea.textContent.trim();
                // Check if there's actual content (more than 10 characters and has non-whitespace)
                const hasText = text.length > 10 && /\S{3,}/.test(text);
                copyBtn.disabled = !hasText;
            });
            observer.observe(textArea, { childList: true, subtree: true, characterData: true });
        }
    }
    
    /**
     * Handle copy button click
     */
    function handleCopy() {
        const textArea = document.querySelector('[data-testid="text-display-area"]');
        if (!textArea) return;
        
        const text = textArea.textContent.trim();
        
        // Copy to clipboard
        navigator.clipboard.writeText(text).then(() => {
            showToast('Kopierat!');
        }).catch(err => {
            console.error('Failed to copy:', err);
            showToast('Kunde inte kopiera text', 'error');
        });
    }
    
    /**
     * Show a toast notification (Material 3 style)
     */
    function showToast(message, type = 'success') {
        // Check if there's already a toast library (Sonner)
        const existingToaster = document.querySelector('[data-sonner-toaster]');
        
        if (existingToaster) {
            // Use existing toast system if available
            const event = new CustomEvent('toast', { detail: { message, type } });
            window.dispatchEvent(event);
            return;
        }
        
        // Create custom toast
        const toast = document.createElement('div');
        toast.className = 'material-toast';
        toast.textContent = message;
        toast.style.cssText = `
            position: fixed;
            top: 24px;
            left: 50%;
            transform: translateX(-50%) translateY(-100px);
            background: var(--md-sys-color-inverse-surface);
            color: var(--md-sys-color-inverse-on-surface);
            padding: 12px 24px;
            border-radius: 8px;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
            z-index: 999999;
            font-weight: 500;
            transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        `;
        
        document.body.appendChild(toast);
        
        // Animate in
        requestAnimationFrame(() => {
            toast.style.transform = 'translateX(-50%) translateY(0)';
        });
        
        // Remove after 3 seconds
        setTimeout(() => {
            toast.style.transform = 'translateX(-50%) translateY(-100px)';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
    
    /**
     * Setup progress indicator for speech recognition
     */
    function setupProgressIndicator() {
        const textArea = document.querySelector('[data-testid="text-display-area"]');
        if (!textArea) {
            setTimeout(setupProgressIndicator, 500);
            return;
        }
        
        // Create progress indicator (hidden by default)
        const progressDiv = document.createElement('div');
        progressDiv.className = 'progress-indicator';
        progressDiv.style.display = 'none';
        progressDiv.innerHTML = `
            <div class="progress-spinner"></div>
            <p class="progress-text">Lyssnar...</p>
        `;
        
        // Insert before text area
        textArea.parentNode.insertBefore(progressDiv, textArea);
        
        // Store reference for later use
        window.progressIndicator = progressDiv;
    }
    
    /**
     * Observe microphone button state to show/hide progress indicator
     */
    function observeMicrophoneState() {
        const micButton = document.querySelector('[data-testid="mic-button"]');
        const micStatus = document.querySelector('[data-testid="mic-status"]');
        
        if (!micButton || !micStatus) {
            setTimeout(observeMicrophoneState, 500);
            return;
        }
        
        // Status text constants
        const STATUS_TEXTS = {
            LISTENING: 'Lyssnar...',
            RECOGNIZING: 'Känner igen tal...'
        };
        
        // Observe status text changes
        const observer = new MutationObserver(() => {
            const status = micStatus.textContent.trim().toUpperCase();
            const progressIndicator = window.progressIndicator;
            
            if (progressIndicator) {
                // Check if microphone is active by checking if button is not disabled
                const isActive = !micButton.disabled && micButton.getAttribute('aria-label') !== 'Starta inspelning';
                
                if (isActive) {
                    progressIndicator.style.display = 'flex';
                    // Update text based on status
                    const progressText = progressIndicator.querySelector('.progress-text');
                    if (status.includes('TAL') || status.includes('SPEAK')) {
                        progressText.textContent = STATUS_TEXTS.RECOGNIZING;
                    } else {
                        progressText.textContent = STATUS_TEXTS.LISTENING;
                    }
                } else {
                    progressIndicator.style.display = 'none';
                }
            }
        });
        
        observer.observe(micStatus, { childList: true, characterData: true, subtree: true });
        observer.observe(micButton, { attributes: true, attributeFilter: ['disabled', 'aria-label'] });
    }
})();
