<aside class="main-sidebar sidebar-dark-primary elevation-4">
    <!-- Brand Logo -->
    <a href="{{url('/')}}" class="brand-link">
        <span class="brand-text font-weight-light">{{ config('app.name', 'Laravel') }}</span>
    </a>

    <!-- Sidebar -->
    <div class="sidebar">

        <!-- Sidebar Menu -->
        <nav class="mt-2">
            <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">
                <li class="nav-item">
                    <a href="{{url('/bracelets')}}" class="nav-link">
                        <i class="far fa-circle nav-icon"></i>
                        <p>@lang('Bracelets')</p>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="{{url('/general-logs')}}" class="nav-link">
                        <i class="far fa-circle nav-icon"></i>
                        <p>@lang('General logs')</p>
                    </a>
                </li>
            </ul>
        </nav>
        <!-- /.sidebar-menu -->
    </div>
    <!-- /.sidebar -->
</aside>
