<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="csrf-token" content="{{ csrf_token() }}">

    <title>{{ config('app.name', 'Laravel') }}</title>


    <!-- Google Font: Source Sans Pro -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
    <!-- Font Awesome Icons -->
    <link rel="stylesheet" href="{{ asset('template/plugins/fontawesome-free/css/all.min.css') }}">
    <!-- Theme style -->
    <link rel="stylesheet" href="{{ asset('template/dist/css/adminlte.min.css') }}">
    <!-- Sweet-Alert  -->
    <link rel="stylesheet" href="{{ asset('template/plugins/sweetalert2/sweetalert2.min.css') }}">

</head>
<body>
<!-- Main content -->
<section class="content">
    <div class="container-fluid">
        <br><br><br>
        <div class="d-flex justify-content-center">
            <div class="row" style="font-size: 25px; padding: 45px;">
                <div class="col-12">
                    @include('layouts.parts.alerts')
                </div>
                @if($bracelet->last_action == 'disabled')
                    <div class="col-12">
                        <p > Your bracelet is currently deactivated, you will not be able to enter or leave the event venue. Press this button to recharge your bracelet and reactivate it.</p>
                    </div>
                    <div class="col-12">
                        <a style="font-size: 25px;" href="{{$bracelet->signed_url_activate}}" class="btn btn-block btn-primary">🚀 Activate my bracelet</a>
                    </div>
                @else
                    <div class="col-12">
                        <p>Your bracelet is currently activated and you are {{($bracelet->last_action === 'in') ? 'inside' : 'outside'}} the event venue.</p>
                    </div>
                @endif
            </div>
        </div>
    </div>
</section>

<!-- REQUIRED SCRIPTS -->

<!-- jQuery -->
<script src="{{ asset('template/plugins/jquery/jquery.min.js') }}"></script>
<!-- Bootstrap 4 -->
<script src="{{ asset('template/plugins/bootstrap/js/bootstrap.bundle.min.js') }} "></script>
<!-- AdminLTE App -->
<script src="{{ asset('template/dist/js/adminlte.min.js') }} "></script>
<!-- Sweet-Alert  -->
<script src="{{asset("template/plugins/sweetalert2/sweetalert2.min.js")}}"></script>

@stack('scripts')

</body>
</html>
