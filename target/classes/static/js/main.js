// Main JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    initializeTooltips();
    
    // Setup navbar active state
    setupNavbarActive();
    
    // Setup event listeners
    setupEventListeners();

    // Initialize self-assessment page if present
    initSelfAssessment();
});

function initializeTooltips() {
    // Bootstrap tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

function setupNavbarActive() {
    const currentUrl = window.location.pathname;
    const navLinks = document.querySelectorAll('.sidebar .nav-link');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentUrl.startsWith(href)) {
            link.classList.add('active');
        }
    });
}

function setupEventListeners() {
    // Search functionality
    const searchInput = document.querySelector('input[placeholder*="Search"]');
    if (searchInput) {
        searchInput.addEventListener('keyup', handleSearch);
    }
    
    // Form submissions
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', handleFormSubmit);
    });
}

function handleSearch(event) {
    const searchTerm = event.target.value.toLowerCase();
    const rows = document.querySelectorAll('tbody tr');
    
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(searchTerm) ? '' : 'none';
    });
}

function handleFormSubmit(event) {
    // Add any custom form handling here
    console.log('Form submitted');
}

// Utility functions
function showNotification(message, type = 'info') {
    console.log(`[${type.toUpperCase()}] ${message}`);
}

function formatDate(date) {
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(date).toLocaleDateString('en-US', options);
}

// Export for use in HTML
window.APP = {
    showNotification,
    formatDate
};

// ============= Self-assessment (8-question UI) =============
function initSelfAssessment() {
    const root = document.getElementById('selfAssessmentRoot');
    if (!root) return;

    const questions = [
        'Over the past 2 weeks, how often have you felt little interest or pleasure in doing things?',
        'Over the past 2 weeks, how often have you felt down, depressed, or hopeless?',
        'Over the past 2 weeks, how often have you had trouble falling or staying asleep, or sleeping too much?',
        'Over the past 2 weeks, how often have you felt tired or had little energy?',
        'Over the past 2 weeks, how often have you had poor appetite or overeating?',
        'Over the past 2 weeks, how often have you felt bad about yourself, or that you are a failure or have let yourself or your family down?',
        'Over the past 2 weeks, how often have you had trouble concentrating on things, such as reading or watching TV?',
        'Over the past 2 weeks, how often have you felt nervous, anxious, or on edge?'
    ];

    const scaleOptions = [
        { emoji: '😊', label: 'Never / Not at all', score: 0 },
        { emoji: '🙂', label: 'Rarely / Slightly', score: 1 },
        { emoji: '😐', label: 'Sometimes / Moderately', score: 2 },
        { emoji: '😟', label: 'Often / Very much', score: 3 },
        { emoji: '😰', label: 'Always / Extremely', score: 4 }
    ];

    const totalQuestions = questions.length;
    const questionTextEl = document.getElementById('assessmentQuestionText');
    const positionEl = document.getElementById('assessmentQuestionPosition');
    const progressTextEl = document.getElementById('assessmentProgressText');
    const progressBarEl = document.getElementById('assessmentProgressBar');
    const optionsContainer = document.getElementById('assessmentOptionsContainer');
    const prevBtn = document.getElementById('assessmentPrevBtn');
    const nextBtn = document.getElementById('assessmentNextBtn');
    const form = document.getElementById('assessmentForm');
    const contentInput = document.getElementById('content');

    let currentIndex = 0;
    const answers = new Array(totalQuestions).fill(null);

    function renderOptions() {
        optionsContainer.innerHTML = '';
        scaleOptions.forEach((opt, idx) => {
            const wrapper = document.createElement('div');
            wrapper.className = 'assessment-option-card';
            wrapper.dataset.index = String(idx);

            if (answers[currentIndex] === idx) {
                wrapper.classList.add('selected');
            }

            const main = document.createElement('div');
            main.className = 'assessment-option-main';

            const emojiSpan = document.createElement('span');
            emojiSpan.className = 'assessment-option-emoji';
            emojiSpan.textContent = opt.emoji;

            const textDiv = document.createElement('div');
            const labelDiv = document.createElement('div');
            labelDiv.className = 'assessment-option-label';
            labelDiv.textContent = opt.label;

            const descDiv = document.createElement('div');
            descDiv.className = 'assessment-option-description';
            descDiv.textContent = `(${opt.score} points)`;

            textDiv.appendChild(labelDiv);
            textDiv.appendChild(descDiv);

            main.appendChild(emojiSpan);
            main.appendChild(textDiv);

            const radio = document.createElement('input');
            radio.type = 'radio';
            radio.name = 'assessmentOption';
            radio.className = 'form-check-input';
            radio.checked = answers[currentIndex] === idx;

            wrapper.appendChild(main);
            wrapper.appendChild(radio);

            wrapper.addEventListener('click', function () {
                answers[currentIndex] = idx;
                renderOptions();
            });

            optionsContainer.appendChild(wrapper);
        });
    }

    function updateView() {
        const questionNumber = currentIndex + 1;
        const percent = Math.round(((questionNumber - 1) / totalQuestions) * 100);

        questionTextEl.textContent = questions[currentIndex];
        positionEl.textContent = `Question ${questionNumber} of ${totalQuestions}`;
        const displayPercent = Math.max(percent, 13);
        progressTextEl.textContent = `${displayPercent}% Complete`;
        progressBarEl.style.width = `${displayPercent}%`;
        progressBarEl.setAttribute('aria-valuenow', String(displayPercent));

        prevBtn.disabled = currentIndex === 0;
        nextBtn.textContent = currentIndex === totalQuestions - 1 ? 'Submit' : 'Next';
        if (currentIndex !== totalQuestions - 1) {
            nextBtn.innerHTML = `Next <i class="bi bi-chevron-right"></i>`;
        }

        renderOptions();
    }

    prevBtn.addEventListener('click', function () {
        if (currentIndex > 0) {
            currentIndex--;
            updateView();
        }
    });

    nextBtn.addEventListener('click', function () {
        if (answers[currentIndex] == null) {
            alert('Please select an option to continue.');
            return;
        }

        if (currentIndex < totalQuestions - 1) {
            currentIndex++;
            updateView();
        } else {
            // Build summary content and submit underlying form
            let totalScore = 0;
            const lines = [];
            questions.forEach((q, idx) => {
                const selectedIdx = answers[idx];
                const opt = scaleOptions[selectedIdx];
                totalScore += opt.score;
                lines.push(`Q${idx + 1}: ${q}`);
                lines.push(`Answer: ${opt.label} (${opt.score} points)`);
                lines.push('');
            });
            lines.push(`Total score: ${totalScore} (0–32)`);

            contentInput.value = lines.join('\n');
            form.submit();
        }
    });

    updateView();
}
