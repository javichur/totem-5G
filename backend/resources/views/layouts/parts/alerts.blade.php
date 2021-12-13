
@if (session('status'))
    <div class="alert alert-success" role="alert">
        {{ session('status') }}
    </div>
@elseif (session('status-error'))
    <div class="alert alert-danger" role="alert">
        {{ session('status-error') }}
    </div>
@endif
