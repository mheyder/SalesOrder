(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('shipping-address', {
            parent: 'entity',
            url: '/shipping-address?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ShippingAddresses'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/shipping-address/shipping-addresses.html',
                    controller: 'ShippingAddressController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('shipping-address-detail', {
            parent: 'entity',
            url: '/shipping-address/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ShippingAddress'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/shipping-address/shipping-address-detail.html',
                    controller: 'ShippingAddressDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'ShippingAddress', function($stateParams, ShippingAddress) {
                    return ShippingAddress.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'shipping-address',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('shipping-address-detail.edit', {
            parent: 'shipping-address-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/shipping-address/shipping-address-dialog.html',
                    controller: 'ShippingAddressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ShippingAddress', function(ShippingAddress) {
                            return ShippingAddress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('shipping-address.new', {
            parent: 'shipping-address',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/shipping-address/shipping-address-dialog.html',
                    controller: 'ShippingAddressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                phone: null,
                                address: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('shipping-address', null, { reload: 'shipping-address' });
                }, function() {
                    $state.go('shipping-address');
                });
            }]
        })
        .state('shipping-address.edit', {
            parent: 'shipping-address',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/shipping-address/shipping-address-dialog.html',
                    controller: 'ShippingAddressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ShippingAddress', function(ShippingAddress) {
                            return ShippingAddress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('shipping-address', null, { reload: 'shipping-address' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('shipping-address.delete', {
            parent: 'shipping-address',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/shipping-address/shipping-address-delete-dialog.html',
                    controller: 'ShippingAddressDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ShippingAddress', function(ShippingAddress) {
                            return ShippingAddress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('shipping-address', null, { reload: 'shipping-address' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
