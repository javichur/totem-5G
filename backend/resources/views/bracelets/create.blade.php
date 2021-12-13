@extends('layouts.app')

@section('content')
    @include('layouts.parts.breadcrumbs', ['title' => 'Bracelets', 'pages' =>[['url' => url('/bracelets'), 'name' => 'Bracelets']] ])

    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header">{{ __('Create bracelet') }}</div>
                <div class="card-body">
                    <form method="POST" action="{{ url('/bracelet/store') }}">
                        @csrf

                        @include('bracelets.form')

                        <button type="submit" class="btn btn-primary">@lang('Save')</button>
                    </form>

                </div>
            </div>
        </div>
    </div>
@endsection

