/**
 * Modern Banking Dashboard Scripts
 * Handles Theme Toggle and Sidebar Toggle
 */

document.addEventListener('DOMContentLoaded', () => {
    // === Theme Toggle Logic ===
    const themeToggleBtn = document.getElementById('themeToggle');
    const storedTheme = localStorage.getItem('theme');

    // Apply stored theme on load
    if (storedTheme) {
        document.documentElement.setAttribute('data-theme', storedTheme);
        updateThemeIcon(storedTheme);
    } else {
        // Check system preference
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            document.documentElement.setAttribute('data-theme', 'dark');
            updateThemeIcon('dark');
        }
    }

    if (themeToggleBtn) {
        themeToggleBtn.addEventListener('click', () => {
            const currentTheme = document.documentElement.getAttribute('data-theme');
            let newTheme = 'light';

            if (currentTheme !== 'dark') {
                newTheme = 'dark';
            }

            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeIcon(newTheme);
        });
    }

    function updateThemeIcon(theme) {
        if (!themeToggleBtn) return;
        const icon = themeToggleBtn.querySelector('i');
        if (theme === 'dark') {
            icon.classList.remove('fa-moon');
            icon.classList.add('fa-sun');
        } else {
            icon.classList.remove('fa-sun');
            icon.classList.add('fa-moon');
        }
    }

    // === Sidebar Toggle Logic ===
    // === Sidebar Toggle Logic ===
    const sidebarToggleBtn = document.getElementById('sidebarToggle');
    const sidebar = document.querySelector('.sidebar');
    const mainContent = document.querySelector('.main-content');

    // Check stored sidebar state
    const sidebarState = localStorage.getItem('sidebarState');
    if (sidebarState === 'collapsed') {
        if (sidebar) sidebar.classList.add('collapsed');
        if (mainContent) mainContent.classList.add('expanded');
    }

    // Toggle Function
    const toggleSidebar = () => {
        if (sidebar) {
            sidebar.classList.toggle('collapsed');
            sidebar.classList.toggle('active'); // Mobile
            if (mainContent) mainContent.classList.toggle('expanded');

            // Save state
            if (sidebar.classList.contains('collapsed')) {
                localStorage.setItem('sidebarState', 'collapsed');
            } else {
                localStorage.setItem('sidebarState', 'expanded');
            }
        }
    };

    if (sidebarToggleBtn) {
        sidebarToggleBtn.addEventListener('click', toggleSidebar);
    }

    // NEW: Inject Floating Toggle (Arrow on middle side)
    if (sidebar && window.innerWidth > 768) { // Only inject on desktop
        const floatBtn = document.createElement('div');
        floatBtn.className = 'sidebar-floating-toggle';
        floatBtn.innerHTML = '<i class="fas fa-chevron-left"></i>';
        floatBtn.title = "Toggle Sidebar";
        sidebar.appendChild(floatBtn);

        floatBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            toggleSidebar();
        });
    }

    // === Live Date Time ===
    function updateDateTime() {
        const dateElement = document.getElementById('liveDateTime');
        if (dateElement) {
            const now = new Date();
            // Format: Mon, Jan 01, 2026 12:00:00 PM
            const options = {
                weekday: 'short',
                year: 'numeric',
                month: 'short',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            };
            dateElement.innerText = now.toLocaleDateString('en-US', options);
        }
    }

    // Run immediately and then every second
    updateDateTime();
    setInterval(updateDateTime, 1000);
});
